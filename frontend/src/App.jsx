import { useEffect, useState } from 'react';
import LoginForm from './components/LoginForm';
import TreePanel from './components/TreePanel';
import MemberPanel from './components/MemberPanel';
import { addRelationship, createMember, createTree, deleteMember, getMembers, getTrees, login, removeRelationship } from './services/api';

export default function App() {
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [trees, setTrees] = useState([]);
  const [selectedTreeId, setSelectedTreeId] = useState(null);
  const [members, setMembers] = useState([]);

  const loadTrees = async () => {
    const data = await getTrees();
    setTrees(data);
    if (data.length && !selectedTreeId) {
      setSelectedTreeId(data[0].id);
    }
  };

  const loadMembers = async (treeId) => {
    if (!treeId) {
      setMembers([]);
      return;
    }
    const data = await getMembers(treeId);
    setMembers(data);
  };

  useEffect(() => {
    if (token) {
      loadTrees();
    }
  }, [token]);

  useEffect(() => {
    if (token && selectedTreeId) {
      loadMembers(selectedTreeId);
    }
  }, [token, selectedTreeId]);

  const handleLogin = async (username, password) => {
    const data = await login(username, password);
    localStorage.setItem('token', data.token);
    setToken(data.token);
  };

  const handleCreateTree = async (name) => {
    await createTree(name);
    await loadTrees();
  };

  const handleCreateMember = async (payload) => {
    await createMember(selectedTreeId, payload);
    await loadMembers(selectedTreeId);
  };


  const handleAddRelationship = async (memberId, payload) => {
    await addRelationship(memberId, payload);
    await loadMembers(selectedTreeId);
  };

  const handleRemoveRelationship = async (memberId, payload) => {
    await removeRelationship(memberId, payload);
    await loadMembers(selectedTreeId);
  };

  const handleDeleteMember = async (memberId) => {
    await deleteMember(memberId);
    await loadMembers(selectedTreeId);
  };

  if (!token) {
    return (
      <main className="container">
        <LoginForm onLogin={handleLogin} />
      </main>
    );
  }

  return (
    <main className="container">
      <h1>Family Tree Management</h1>
      <div className="layout">
        <TreePanel
          trees={trees}
          selectedTreeId={selectedTreeId}
          onSelectTree={setSelectedTreeId}
          onCreateTree={handleCreateTree}
        />
        <MemberPanel
          selectedTreeId={selectedTreeId}
          members={members}
          onCreateMember={handleCreateMember}
          onDeleteMember={handleDeleteMember}
          onAddRelationship={handleAddRelationship}
          onRemoveRelationship={handleRemoveRelationship}
        />
      </div>
    </main>
  );
}
