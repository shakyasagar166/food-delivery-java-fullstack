import axios from 'axios';

export const API_URL = (process.env.REACT_APP_API_URL || "https://food-delivery-java-fullstack-4.onrender.com").replace(/\/+$/, '');

export const api = axios.create({
  baseURL: API_URL, 
  headers: {
    'Content-Type': 'application/json',
  },
});
