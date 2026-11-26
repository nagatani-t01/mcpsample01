import * as React from 'react';

import { IRouter, Link } from 'react-router';
import { url } from '../../util';

import { IVet } from '../../types';

interface IVetsPageState {
  vets: IVet[];
}

export default class VetsPage extends React.Component<void, IVetsPageState> {
  constructor() {
    super();

    this.state = { vets: [] };
  }

  componentDidMount() {
    const requestUrl = url('api/vets');

    fetch(requestUrl)
      .then(response => response.json())
      .then(vets => { console.log('vets', vets); this.setState({ vets }); });
  }

  render() {
    const { vets } = this.state;

    if (!vets) {
      return <h2>獣医師</h2>;
    }

    return (
      <span>
        <h2>獣医師</h2>
        <table className='table table-striped'>
          <thead>
            <tr>
              <th>氏名</th>
              <th>専門</th>
            </tr>
          </thead>
          <tbody>

            {vets.map(vet => (
              <tr key={vet.id}>
                <td>{vet.lastName} {vet.firstName}</td>
                <td>{vet.specialties.length > 0 ? vet.specialties.map(specialty => specialty.name).join('、') : 'なし'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </span>
    );
  }
}
