import apiClient from "../../../api/client.js";

export async function getMyGroups() {
  const { data } = await apiClient.get("/grupo");
  return Array.isArray(data) ? data : [];
}

export async function createGroup(payload) {
  const { data } = await apiClient.post("/grupo/add", payload);
  return data;
}

export async function updateGroup(id, payload) {
  const { data } = await apiClient.put(`/grupo/update/${id}`, payload);
  return data;
}

export async function toggleGroupActive(id, activo) {
  const { data } = await apiClient.patch(`/grupo/active/${id}`, { activo });
  return data;
}

export async function deleteGroup(id) {
  await apiClient.delete(`/grupo/delete/${id}`);
}

export async function leaveGroup(id) {
  await apiClient.delete(`/grupo/${id}/leave`);
}

export async function joinGroup(payload) {
  const { data } = await apiClient.post("/grupo/join", payload);
  return data;
}

export async function getGroupMembers(groupId) {
  const { data } = await apiClient.get(`/grupo/${groupId}/miembros`);
  return Array.isArray(data) ? data : [];
}

export async function getGroupAssignments(groupId) {
  const { data } = await apiClient.get(`/grupo/${groupId}/asignaciones`);
  return Array.isArray(data) ? data : [];
}

export async function getGroupAssignmentDetail(groupId, assignmentId) {
  const { data } = await apiClient.get(`/grupo/${groupId}/asignaciones/${assignmentId}`);
  return data;
}

export async function createGroupAssignment(groupId, payload) {
  const { data } = await apiClient.post(`/grupo/${groupId}/asignaciones/add`, payload);
  return data;
}

export async function addGroupMember(groupId, payload) {
  const { data } = await apiClient.post(`/grupo/${groupId}/miembros/add`, payload);
  return data;
}

export async function removeGroupMember(groupId, userId) {
  await apiClient.delete(`/grupo/${groupId}/miembros/delete/${userId}`);
}

export async function updateGroupMemberRole(groupId, userId, payload) {
  const { data } = await apiClient.patch(`/grupo/${groupId}/miembros/rol/${userId}`, payload);
  return data;
}

export async function transferGroupOwnership(groupId, payload) {
  const { data } = await apiClient.patch(`/grupo/${groupId}/transfer-ownership`, payload);
  return data;
}

export async function getInvitationCode(id) {
  const { data } = await apiClient.get(`/grupo/${id}/invitation-code`);
  return data;
}

export async function regenerateInvitationCode(id) {
  const { data } = await apiClient.post(`/grupo/${id}/invitation-code/regenerate`);
  return data;
}
