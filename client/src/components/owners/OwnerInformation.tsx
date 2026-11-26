import * as React from 'react';

import { Link } from 'react-router';
import { IOwner } from '../../types';

export default ({ owner }: { owner: IOwner }) => (
  <section>
    <h2>飼い主情報</h2>

    <table className='table table-striped'>
      <tbody>
        <tr>
          <th>氏名</th>
          <td><b>{owner.lastName} {owner.firstName}</b></td>
        </tr>
        <tr>
          <th>住所</th>
          <td>{owner.address}</td>
        </tr>
        <tr>
          <th>市区町村</th>
          <td>{owner.city}</td>
        </tr>
        <tr>
          <th>電話番号</th>
          <td>{owner.telephone}</td>
        </tr>
      </tbody>
    </table>

    <Link to={`/owners/${owner.id}/edit`} className='btn btn-default'>飼い主を編集</Link>
    &nbsp;
    <Link to={`/owners/${owner.id}/pets/new`} className='btn btn-default'>ペットを追加</Link>
  </section>
);
