import * as React from 'react';

import { url } from '../util';

interface IShownError {
  status?: string;
  message?: string;
  className?: string;
  exMessage?: string;
}

interface IErrorPageState {
  error?: IShownError;
}

export default class ErrorPage extends React.Component<void, IErrorPageState> {
  constructor() {
    super();
    this.state = {};
  }

  componentDidMount() {
    fetch(url('api/oops'))
      .then(response => response.json().then(body => ({ status: response.status, body })))
      .then(result => this.setState({
        error: {
          status: String(result.status),
          message: result.body.message,
          className: result.body.className,
          exMessage: result.body.exMessage
        }
      }))
      .catch(() => this.setState({ error: { message: 'The error request could not be completed.' } }));
  }

  render() {
    const { error } = this.state;

    return <span>
      <img src='/images/pets.png' />

      <h2>Something happened...</h2>
      { error ?
        <span>
          <p><b>Status:</b> {error.status}</p>
          <p><b>Message:</b> {error.exMessage || error.message}</p>
          {error.className ? <p><b>Type:</b> {error.className}</p> : null}
        </span>
        :
        <p><b>Unknown error</b></p>
      }
    </span>;
  }
};
