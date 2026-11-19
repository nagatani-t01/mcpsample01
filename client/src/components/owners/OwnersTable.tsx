import * as React from 'react';

import { IOwner } from '../../types';

const renderRow = (owner: IOwner) => (
  <tr key={owner.id}>
    <td>
      <a href={`/owners/${owner.id}`}>
        {owner.firstName} {owner.lastName}
      </a>
    </td>
    <td className='hidden-sm hidden-xs'>{owner.address}</td>
    <td>{owner.city}</td>
    <td>{owner.telephone}</td>
    <td className='hidden-xs'>{owner.pets.map(pet => pet.name).join(', ')}</td>
  </tr>
);

const renderOwners = (owners: IOwner[]) => (
  <section>
    <h2>{owners.length} Owners found</h2>
    <table className='table table-striped'>
      <thead>
        <tr>
          <th>飼い主</th>
          <th className='hidden-sm hidden-xs'>Address</th>
          <th>住所２</th>
          <th>電話番号</th>
          <th className='hidden-xs'>Pets</th>
        </tr>
      </thead>
      <tbody>
        {owners.map(renderRow)}
      </tbody>
    </table>
  </section>
);

export default ({ owners }: { owners: IOwner[] }) => owners ? renderOwners(owners) : null;
