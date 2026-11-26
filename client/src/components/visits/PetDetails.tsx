import * as React from 'react';

import { IOwner, IPet } from '../../types';

export default ({ owner, pet }: { owner: IOwner, pet: IPet }) => (
  <table className='table table-striped'>
    <thead>
      <tr>
        <th>ペット名</th>
        <th>生年月日</th>
        <th>種別</th>
        <th>飼い主</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>{pet.name}</td>
        <td>{pet.birthDate}</td>
        <td>{pet.type.name}</td>
        <td>{owner.lastName} {owner.firstName}</td>
      </tr>
    </tbody>
  </table>
);
