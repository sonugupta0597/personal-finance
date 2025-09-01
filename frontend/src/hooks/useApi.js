import { useState, useCallback } from "react";
import { getToken } from "../utils/authUtils";
import config from "../config";


export default function useApi() {
const [loading, setLoading] = useState(false);
const [error, setError] = useState(null);


const request = useCallback(async (path, options = {}) => {
setLoading(true); setError(null);
try {
const token = getToken();
const res = await fetch(`${config.API_BASE_URL}${path}`, {
headers: {
"Content-Type": "application/json",
...(token ? { Authorization: `Bearer ${token}` } : {}),
...(options.headers || {})
},
...options
});
const data = await res.json().catch(() => ({}));
if (!res.ok) throw new Error(data.message || `HTTP ${res.status}`);
return data;
} catch (e) {
setError(e.message);
throw e;
} finally {
setLoading(false);
}
}, []);


return { request, loading, error };
}