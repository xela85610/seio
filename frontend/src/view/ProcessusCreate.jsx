import React, {useEffect, useMemo, useRef, useState} from 'react';
import { apiGet, apiPost } from '../service/Api'; // adapte le chemin
import '../style/ProcessusCreate.css';

function toISO(dateStr) { return dateStr ? new Date(dateStr).toISOString() : undefined; }
const PRIORITIES = ['LOW', 'MEDIUM', 'HIGH'];

export default function ProcessusCreate() {
    const [users, setUsers] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loadingRef, setLoadingRef] = useState(true);
    const [errorRef, setErrorRef] = useState(null);

    const [form, setForm] = useState({
        name: '',
        description: '',
        categoryId: '',
        priority: 'MEDIUM',
        startDate: '',
        dueDate: '',
        ownerId: '',
        steps: [{ title: '', assigneeId: '', dueDate: '', order: 1 }],
    });

    const [submitting, setSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState(null);
    const [createdId, setCreatedId] = useState(null);
    const didInit = useRef(false);

    useEffect(() => {
        if (didInit.current) return;
        didInit.current = true;

        (async () => {
            try {
                const [u, c] = await Promise.all([
                    apiGet('/users'),
                    apiGet('/process-categories'),
                ]);
                setUsers(u);
                setCategories(c);
            } catch (err) {
                console.error('Ref data error:', err);
                setErrorRef(err.body?.error || err.body?.message || err.message);
            } finally {
                setLoadingRef(false);
            }
        })();
    }, []);

    const ownerOptions = useMemo(() => users, [users]);

    function updateField(key, value) { setForm(prev => ({ ...prev, [key]: value })); }
    function updateStep(idx, patch) {
        setForm(prev => {
            const steps = [...prev.steps];
            steps[idx] = { ...steps[idx], ...patch };
            return { ...prev, steps };
        });
    }
    function addStep() {
        setForm(prev => {
            const nextOrder = (prev.steps?.length || 0) + 1;
            return { ...prev, steps: [...prev.steps, { title: '', assigneeId: '', dueDate: '', order: nextOrder }] };
        });
    }
    function removeStep(idx) {
        setForm(prev => {
            const steps = prev.steps.filter((_, i) => i !== idx).map((s, i) => ({ ...s, order: i + 1 }));
            return { ...prev, steps: steps.length ? steps : [{ title: '', assigneeId: '', dueDate: '', order: 1 }] };
        });
    }

    function validate(data) {
        const errors = [];
        if (!data.name?.trim()) errors.push('Le nom est requis.');
        if (data.startDate && data.dueDate && new Date(data.startDate) > new Date(data.dueDate)) {
            errors.push('La date de fin doit être postérieure à la date de début.');
        }
        if (data.steps?.length) {
            data.steps.forEach((s, i) => {
                if (!s.title?.trim()) errors.push(`L’étape #${i + 1} doit avoir un titre.`);
            });
        }
        return errors;
    }

    async function onSubmit(e) {
        e.preventDefault();
        setSubmitError(null);
        setCreatedId(null);

        const payload = {
            name: form.name?.trim(),
            description: form.description?.trim() || undefined,
            categoryId: form.categoryId || undefined,
            priority: form.priority || 'MEDIUM',
            startDate: toISO(form.startDate),
            dueDate: toISO(form.dueDate),
            ownerId: form.ownerId || undefined,
            steps: (form.steps || []).map((s, i) => ({
                title: s.title?.trim(),
                assigneeId: s.assigneeId || undefined,
                dueDate: toISO(s.dueDate),
                order: i + 1,
            })),
        };

        const errs = validate(payload);
        if (errs.length) { setSubmitError(errs.join(' ')); return; }

        setSubmitting(true);
        try {
            const created = await apiPost('/processes', payload);
            setCreatedId(created?.id || '(inconnu)');
            setForm({
                name: '',
                description: '',
                categoryId: '',
                priority: 'MEDIUM',
                startDate: '',
                dueDate: '',
                ownerId: '',
                steps: [{ title: '', assigneeId: '', dueDate: '', order: 1 }],
            });
        } catch (e) {
            setSubmitError(e?.message || 'Erreur lors de la création du processus.');
        } finally {
            setSubmitting(false);
        }
    }

    if (loadingRef) return <div className="process-page">Chargement…</div>;
    if (errorRef) return <div className="process-page"><div className="alert-error">Erreur: {errorRef}</div></div>;

    return (
        <div className="process-page">
            <form className="process-form" onSubmit={onSubmit}>
                <h2>Créer un processus</h2>

                {submitError && <div className="alert-error">{submitError}</div>}
                {createdId && <div className="alert-success">Processus créé avec l’ID: {createdId}</div>}

                <div className="form-row">
                    <label className="label">Nom *</label>
                    <input
                        className="input"
                        type="text"
                        value={form.name}
                        onChange={e => updateField('name', e.target.value)}
                        placeholder="Ex: Onboarding d’un nouveau client"
                        required
                    />
                </div>

                <div className="form-row">
                    <label className="label">Description</label>
                    <textarea
                        className="textarea"
                        value={form.description}
                        onChange={e => updateField('description', e.target.value)}
                        placeholder="Détails du processus…"
                        rows={4}
                    />
                </div>

                <div className="form-grid">
                    <div>
                        <label className="label">Catégorie</label>
                        <select
                            className="select"
                            value={form.categoryId}
                            onChange={e => updateField('categoryId', e.target.value)}
                        >
                            <option value="">—</option>
                            {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                        </select>
                        <div className="text-muted">Optionnel</div>
                    </div>

                    <div>
                        <label className="label">Priorité</label>
                        <select
                            className="select"
                            value={form.priority}
                            onChange={e => updateField('priority', e.target.value)}
                        >
                            {PRIORITIES.map(p => <option key={p} value={p}>{p}</option>)}
                        </select>
                    </div>

                    <div>
                        <label className="label">Propriétaire</label>
                        <select
                            className="select"
                            value={form.ownerId}
                            onChange={e => updateField('ownerId', e.target.value)}
                        >
                            <option value="">—</option>
                            {ownerOptions.map(u => <option key={u.id} value={u.id}>{u.displayName}</option>)}
                        </select>
                    </div>
                </div>

                <div className="form-grid">
                    <div>
                        <label className="label">Début</label>
                        <input
                            className="input"
                            type="date"
                            value={form.startDate}
                            onChange={e => updateField('startDate', e.target.value)}
                        />
                    </div>
                    <div>
                        <label className="label">Échéance</label>
                        <input
                            className="input"
                            type="date"
                            value={form.dueDate}
                            onChange={e => updateField('dueDate', e.target.value)}
                        />
                    </div>
                </div>

                <fieldset className="fieldset">
                    <legend>Étapes</legend>
                    {(form.steps || []).map((s, idx) => (
                        <div key={idx} className="step-row">
                            <div>
                                <label className="label">Titre *</label>
                                <input
                                    className="input"
                                    type="text"
                                    value={s.title}
                                    onChange={e => updateStep(idx, { title: e.target.value })}
                                    placeholder={`Étape ${idx + 1}`}
                                    required
                                />
                            </div>
                            <div>
                                <label className="label">Assigné à</label>
                                <select
                                    className="select"
                                    value={s.assigneeId}
                                    onChange={e => updateStep(idx, { assigneeId: e.target.value })}
                                >
                                    <option value="">—</option>
                                    {users.map(u => <option key={u.id} value={u.id}>{u.displayName}</option>)}
                                </select>
                            </div>
                            <div>
                                <label className="label">Échéance</label>
                                <input
                                    className="input"
                                    type="date"
                                    value={s.dueDate}
                                    onChange={e => updateStep(idx, { dueDate: e.target.value })}
                                />
                            </div>
                            <div style={{ alignSelf: 'end' }}>
                                <button type="button" className="button button-secondary" onClick={() => removeStep(idx)} disabled={(form.steps || []).length <= 1}>
                                    Supprimer
                                </button>
                            </div>
                        </div>
                    ))}
                    <button type="button" className="button button-secondary" onClick={addStep}>+ Ajouter une étape</button>
                </fieldset>

                <div className="actions">
                    <button type="submit" className="button" disabled={submitting}>
                        {submitting ? 'Enregistrement…' : 'Créer le processus'}
                    </button>
                </div>
            </form>
        </div>
    );
}
