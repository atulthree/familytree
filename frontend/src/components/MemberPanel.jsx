import { useMemo, useState } from 'react';

const defaultForm = {
  name: '',
  surname: '',
  sex: 'MALE',
  dob: '',
  image: ''
};

const relationOptions = [
  { label: 'Spouse', value: 'SPOUSE' },
  { label: 'Parent', value: 'PARENT' },
  { label: 'Child', value: 'CHILD' }
];

const NODE_WIDTH = 220;
const NODE_HEIGHT = 120;
const H_GAP = 70;
const V_GAP = 90;
const PADDING = 30;

const memberLabel = (member) => `${member.name} ${member.surname}`.trim();

const buildTreeLayout = (members) => {
  if (!members.length) {
    return { positions: new Map(), width: 0, height: 0, links: [], spouseLinks: [] };
  }

  const byId = new Map(members.map((member) => [member.id, member]));
  const levels = new Map(members.map((member) => [member.id, 0]));

  for (let i = 0; i < members.length; i += 1) {
    members.forEach((member) => {
      const parentIds = (member.parents || []).map((parent) => parent.id).filter((id) => byId.has(id));
      const maxParentLevel = parentIds.reduce((max, parentId) => Math.max(max, levels.get(parentId) ?? 0), 0);
      const expectedLevel = parentIds.length ? maxParentLevel + 1 : 0;
      if (expectedLevel > (levels.get(member.id) ?? 0)) {
        levels.set(member.id, expectedLevel);
      }
    });
  }

  const groupedByLevel = new Map();
  members.forEach((member) => {
    const level = levels.get(member.id) ?? 0;
    if (!groupedByLevel.has(level)) {
      groupedByLevel.set(level, []);
    }
    groupedByLevel.get(level).push(member);
  });

  const sortedLevels = Array.from(groupedByLevel.keys()).sort((a, b) => a - b);
  sortedLevels.forEach((level) => {
    groupedByLevel.get(level).sort((a, b) => a.id - b.id);
  });

  const maxPerLevel = Math.max(...sortedLevels.map((level) => groupedByLevel.get(level).length));
  const width = PADDING * 2 + maxPerLevel * NODE_WIDTH + Math.max(0, maxPerLevel - 1) * H_GAP;
  const height = PADDING * 2 + sortedLevels.length * NODE_HEIGHT + Math.max(0, sortedLevels.length - 1) * V_GAP;

  const positions = new Map();
  sortedLevels.forEach((level) => {
    const row = groupedByLevel.get(level);
    const rowWidth = row.length * NODE_WIDTH + Math.max(0, row.length - 1) * H_GAP;
    const startX = (width - rowWidth) / 2;
    row.forEach((member, index) => {
      positions.set(member.id, {
        x: startX + index * (NODE_WIDTH + H_GAP),
        y: PADDING + level * (NODE_HEIGHT + V_GAP)
      });
    });
  });

  const links = [];
  members.forEach((member) => {
    const childPos = positions.get(member.id);
    if (!childPos) return;

    (member.parents || []).forEach((parent) => {
      const parentPos = positions.get(parent.id);
      if (!parentPos) return;
      links.push({
        key: `p-${parent.id}-${member.id}`,
        x1: parentPos.x + NODE_WIDTH / 2,
        y1: parentPos.y + NODE_HEIGHT,
        x2: childPos.x + NODE_WIDTH / 2,
        y2: childPos.y
      });
    });
  });

  const spouseLinks = [];
  members.forEach((member) => {
    if (!member.spouse || member.id > member.spouse.id) return;
    const pos1 = positions.get(member.id);
    const pos2 = positions.get(member.spouse.id);
    if (!pos1 || !pos2) return;
    spouseLinks.push({
      key: `s-${member.id}-${member.spouse.id}`,
      x1: pos1.x + NODE_WIDTH,
      y1: pos1.y + NODE_HEIGHT / 2,
      x2: pos2.x,
      y2: pos2.y + NODE_HEIGHT / 2
    });
  });

  return { positions, width, height, links, spouseLinks };
};

export default function MemberPanel({
  selectedTreeId,
  members,
  onCreateMember,
  onDeleteMember,
  onAddRelationship,
  onRemoveRelationship
}) {
  const [form, setForm] = useState(defaultForm);
  const [activeMemberId, setActiveMemberId] = useState(null);
  const [relationType, setRelationType] = useState('SPOUSE');
  const [targetMemberId, setTargetMemberId] = useState('');

  const layout = useMemo(() => buildTreeLayout(members), [members]);

  if (!selectedTreeId) {
    return <section className="card">Select a tree to manage members.</section>;
  }

  const getRelationshipNames = (member, type) => {
    if (type === 'SPOUSE') return member.spouse ? [memberLabel(member.spouse)] : [];
    if (type === 'PARENT') return (member.parents || []).map(memberLabel);
    return (member.children || []).map(memberLabel);
  };

  const handleRelationshipAction = async (mode) => {
    if (!activeMemberId || !targetMemberId) return;
    const payload = { type: relationType, targetMemberId: Number(targetMemberId) };
    if (mode === 'add') {
      await onAddRelationship(activeMemberId, payload);
    } else {
      await onRemoveRelationship(activeMemberId, payload);
    }
    setTargetMemberId('');
  };

  return (
    <section className="card">
      <h3>Members</h3>
      <div className="grid">
        <input placeholder="Name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        <input placeholder="Surname" value={form.surname} onChange={(e) => setForm({ ...form, surname: e.target.value })} />
        <select value={form.sex} onChange={(e) => setForm({ ...form, sex: e.target.value })}>
          <option value="MALE">Male</option>
          <option value="FEMALE">Female</option>
          <option value="OTHER">Other</option>
        </select>
        <input type="date" value={form.dob} onChange={(e) => setForm({ ...form, dob: e.target.value })} />
        <input placeholder="Image URL" value={form.image} onChange={(e) => setForm({ ...form, image: e.target.value })} />
      </div>
      <button
        onClick={() => {
          onCreateMember({
            name: form.name,
            surname: form.surname,
            sex: form.sex,
            dob: form.dob || null,
            image: form.image || null,
            spouseId: null,
            parentIds: [],
            childIds: []
          });
          setForm(defaultForm);
        }}
      >
        Add Node
      </button>

      <div className="tree-canvas" style={{ height: Math.max(layout.height, 260) }}>
        <svg className="tree-links" viewBox={`0 0 ${Math.max(layout.width, 320)} ${Math.max(layout.height, 260)}`}>
          {layout.links.map((link) => (
            <line
              key={link.key}
              className="parent-link"
              x1={link.x1}
              y1={link.y1}
              x2={link.x2}
              y2={link.y2}
            />
          ))}
          {layout.spouseLinks.map((link) => (
            <line
              key={link.key}
              className="spouse-link"
              x1={link.x1}
              y1={link.y1}
              x2={link.x2}
              y2={link.y2}
            />
          ))}
        </svg>

        {members.map((member) => {
          const pos = layout.positions.get(member.id) ?? { x: PADDING, y: PADDING };
          const isActive = activeMemberId === member.id;
          const eligibleTargets = members.filter((candidate) => candidate.id !== member.id);

          return (
            <article
              key={member.id}
              className="member-node"
              style={{ left: pos.x, top: pos.y, width: NODE_WIDTH, minHeight: NODE_HEIGHT }}
              onMouseEnter={() => setActiveMemberId(member.id)}
              onMouseLeave={() => setActiveMemberId((current) => (current === member.id ? null : current))}
            >
              <header>
                <strong>{memberLabel(member)}</strong>
                <span>#{member.id}</span>
              </header>
              <small>{member.sex}</small>
              <div className="node-relations">
                <div>Spouse: {getRelationshipNames(member, 'SPOUSE').join(', ') || '—'}</div>
                <div>Parents: {getRelationshipNames(member, 'PARENT').join(', ') || '—'}</div>
                <div>Children: {getRelationshipNames(member, 'CHILD').join(', ') || '—'}</div>
              </div>

              {isActive && (
                <div className="node-actions">
                  <select value={relationType} onChange={(e) => setRelationType(e.target.value)}>
                    {relationOptions.map((option) => (
                      <option key={option.value} value={option.value}>{option.label}</option>
                    ))}
                  </select>
                  <select value={targetMemberId} onChange={(e) => setTargetMemberId(e.target.value)}>
                    <option value="">Choose member</option>
                    {eligibleTargets.map((candidate) => (
                      <option key={candidate.id} value={candidate.id}>{memberLabel(candidate)}</option>
                    ))}
                  </select>
                  <div className="row">
                    <button type="button" onClick={() => handleRelationshipAction('add')}>Add Relationship</button>
                    <button type="button" onClick={() => handleRelationshipAction('remove')}>Delete Relationship</button>
                    <button type="button" onClick={() => onDeleteMember(member.id)}>Delete Node</button>
                  </div>
                </div>
              )}
            </article>
          );
        })}
      </div>
    </section>
  );
}
