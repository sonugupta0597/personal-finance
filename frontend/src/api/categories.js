import config from "../config";
import { getToken } from "../utils/authUtils";


export const listCategories = async () => {
const res = await fetch(`${config.API_BASE_URL}/categories`, { headers: { Authorization: `Bearer ${getToken()}` } });
if (!res.ok) throw new Error("Failed to load categories");
return res.json();
};