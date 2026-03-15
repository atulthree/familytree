import { useState } from 'react';

export default function TreePanel({ trees, selectedTreeId, onSelectTree, onCreateTree }) {
  const [newTreeName, setNewTreeName] = useState('');

  return (
    <section className="card">
      <h3>Family Trees</h3>
      <div className="row">
        <input
          value={newTreeName}
          onChange={(e) => setNewTreeName(e.target.value)}
          placeholder="New tree name"
        />
        <button
          onClick={() => {
            if (!newTreeName.trim()) return;
            onCreateTree(newTreeName.trim());
            setNewTreeName('');
          }}
        >
          Create
        </button>
      </div>
      <ul>
        {trees.map((tree) => (
          <li key={tree.id}>
            <button
              className={selectedTreeId === tree.id ? 'selected' : ''}
              onClick={() => onSelectTree(tree.id)}
            >
              {tree.name}
            </button>
          </li>
        ))}
      </ul>
    </section>
  );
}
