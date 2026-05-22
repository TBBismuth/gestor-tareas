import apiClient from "../../../api/client.js";

export async function getMyCategories() {
  const { data } = await apiClient.get("/categoria");
  return Array.isArray(data) ? data : [];
}
