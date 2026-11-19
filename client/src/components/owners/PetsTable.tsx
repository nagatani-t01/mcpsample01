import * as React from 'react';

import { Link } from 'react-router';
import { IOwner, IPet } from '../../types';

const VisitsTable = ({ ownerId, pet }: { ownerId: number, pet: IPet }) => (
  <table className='table-condensed'>
    <thead>
      <tr>
        <th>来店日</th>
        <th>来店内容</th>
      </tr>
    </thead>
    <tbody>
      {pet.visits.map(visit => (
        <tr key={visit.id}>
          <td>{visit.date}</td>
          <td>{visit.description}</td>
        </tr>
      ))}
      <tr>
        <td>
          <Link to={`/owners/${ownerId}/pets/${pet.id}/edit`}>Edit Pet</Link>
        </td>
        <td>
          <Link to={`/owners/${ownerId}/pets/${pet.id}/visits/new`}>Add Visit</Link>
        </td>
      </tr>
    </tbody>
  </table>
);

export default ({ owner }: { owner: IOwner }) => (
  <section>
    <h2>ペットと来店</h2>
    <table className='table table-striped'>
      <tbody>
        {owner.pets.map(pet => (
          <tr key={pet.id}>
            <td style={{ 'verticalAlign': 'top' }}>
              <dl className='dl-horizontal'>
                <dt>ペット名</dt>
                <dd>{pet.name}</dd>
                <dt>誕生日</dt>
                <dd>{pet.birthDate}</dd>
                <dt>ペット種類</dt>
                <dd>{pet.type.name}</dd>
              </dl>
            </td>
            <td style={{ 'verticalAlign': 'top' }}>
              <VisitsTable ownerId={owner.id} pet={pet} />
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  </section>
);
