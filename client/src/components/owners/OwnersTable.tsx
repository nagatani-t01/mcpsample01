import * as React from 'react';

import { IOwner } from '../../types';

const renderRow = (owner: IOwner) => (
  <tr key={owner.id}>
    <td>
      <a href={`/owners/${owner.id}`}>
        {owner.lastName} {owner.firstName}
      </a>
    </td>
    <td className='hidden-sm hidden-xs'>{owner.address}</td>
    <td>{owner.city}</td>
    <td>{owner.telephone}</td>
    <td className='hidden-xs'>{owner.pets.map(pet => pet.name).join(', ')}</td>
    <td className='hidden-xs'>{owner.pets.map(pet => pet.type && pet.type.name).filter(Boolean).join(', ')}</td>
  </tr>
);

const renderOwners = (owners: IOwner[]) => (
  <section>
    <h2>{owners.length} 件の飼い主が見つかりました</h2>
    <table className='table table-striped'>
      <thead>
        <tr>
          <th>氏名</th>
          <th className='hidden-sm hidden-xs'>住所</th>
          <th>市区町村</th>
          <th>電話番号</th>
          <th className='hidden-xs'>ペット</th>
          <th className='hidden-xs'>ペットの種類</th>
        </tr>
      </thead>
      <tbody>
        {owners.map(renderRow)}
      </tbody>
    </table>
  </section>
);

export default ({ owners }: { owners: IOwner[] }) => owners ? renderOwners(owners) : null;
