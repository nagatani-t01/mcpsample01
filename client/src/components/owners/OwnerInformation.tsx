import * as React from 'react';

import { Link } from 'react-router';
import { IOwner } from '../../types';

export default ({ owner }: { owner: IOwner }) => (
  <section>
    <h2>Owner Information</h2>

    <table className='table table-striped'>
      <tbody>
        <tr>
          <th>飼い主名</th>
          <td><b>{owner.firstName} {owner.lastName}</b></td>
        </tr>
        <tr>
          <th>住所１</th>
          <td>{owner.address}</td>
        </tr>
        <tr>
          <th>住所２</th>
          <td>{owner.city}</td>
        </tr>
        <tr>
          <th>電話番号</th>
          <td>{owner.telephone}</td>
        </tr>
      </tbody>
    </table>

    <Link to={`/owners/${owner.id}/edit`} className='btn btn-default'>Edit Owner</Link>
    &nbsp;
    <Link to={`/owners/${owner.id}/pets/new`} className='btn btn-default'>Add New Pet</Link>
  </section>
);
