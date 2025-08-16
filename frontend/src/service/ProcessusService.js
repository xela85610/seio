// src/services/processApi.js
import { apiGet, apiPost } from './Api';

// Récupération des données de référence
export const fetchUsers = () => apiGet('/processes/users');
export const fetchProcessCategories = () => apiGet('/processes/process-categories');

// Création d’un processus
export const createProcess = (payload) => apiPost('/processes', payload);
