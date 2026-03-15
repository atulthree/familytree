import { useMemo, useState } from 'react';

const defaultForm = {
  name: '',
  surname: '',
  sex: 'MALE',
  dob: '',
  image: '',
  spouseId: '',
  parentIds: '',
  childIds: ''
};

const parseIds = (value) =>
  value
    .split(',')
    .map((id) => id.trim())
    .filter(Boolean)
    .map((id) => Number(id));

export default function MemberPanel({ selectedTreeId, members, onCreateMember, onDeleteMember }) {
  const [form, setForm] = useState(defaultForm);
  const memberIdList = useMemo(() => members.map((m) => m.id).join(', '), [members]);

  if (!selectedTreeId) {
    return <section className="card">Select a tree to manage members.</section>;
  }

  return (
    <section className="card">
      <h3>Members</h3>
      <p>Existing member IDs: {memberIdList || 'none yet'}</p>
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
        <input placeholder="Spouse ID" value={form.spouseId} onChange={(e) => setForm({ ...form, spouseId: e.target.value })} />
        <input placeholder="Parent IDs (comma-separated)" value={form.parentIds} onChange={(e) => setForm({ ...form, parentIds: e.target.value })} />
        <input placeholder="Child IDs (comma-separated)" value={form.childIds} onChange={(e) => setForm({ ...form, childIds: e.target.value })} />
      </div>
      <button
        onClick={() => {
          onCreateMember({
            name: form.name,
            surname: form.surname,
            sex: form.sex,
            dob: form.dob || null,
            image: form.image || null,
            spouseId: form.spouseId ? Number(form.spouseId) : null,
            parentIds: form.parentIds ? parseIds(form.parentIds) : [],
            childIds: form.childIds ? parseIds(form.childIds) : []
          });
          setForm(defaultForm);
        }}
      >
        Add Member
      </button>

      <ul>
        {members.map((member) => (
          <li key={member.id} className="member-row">
            <span>
              #{member.id} {member.name} {member.surname} ({member.sex})
            </span>
            <button onClick={() => onDeleteMember(member.id)}>Remove</button>
          </li>
        ))}
      </ul>
    </section>
  );
}
