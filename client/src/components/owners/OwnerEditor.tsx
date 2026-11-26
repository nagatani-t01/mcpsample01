import * as React from 'react';

import { IRouter, Link } from 'react-router';
import { url, submitForm } from '../../util';

import Input from '../form/Input';

import { Digits, NotEmpty } from '../form/Constraints';

import { IInputChangeHandler, IFieldError, IError, IOwner, IRouterContext } from '../../types';

interface IOwnerEditorProps {
  initialOwner?: IOwner;
}

interface IOwnerEditorState {
  owner?: IOwner;
  error?: IError;
};

export default class OwnerEditor extends React.Component<IOwnerEditorProps, IOwnerEditorState> {

  context: IRouterContext;

  static contextTypes = {
    router: React.PropTypes.object.isRequired
  };

  constructor(props) {
    super(props);
    this.onInputChange = this.onInputChange.bind(this);
    this.onSubmit = this.onSubmit.bind(this);

    this.state = {
      owner: Object.assign({}, props.initialOwner)
    };
  }

  onSubmit(event) {
    event.preventDefault();

    const { owner } = this.state;

    const url = owner.isNew ? '/api/owners' : '/api/owners/' + owner.id;
    submitForm(owner.isNew ? 'POST' : 'PUT', url, owner, (status, response) => {
      if (status === 200 || status === 201 || status === 204) {
        const savedId = response && response.id ? response.id : owner.id;
        this.context.router.push({
          pathname: '/owners/' + savedId
        });
      } else {
        console.log('ERROR?!...', response);
        this.setState({ error: response });
      }
    });
  }

  onInputChange(name: string, value: string, fieldError: IFieldError) {
    const { owner, error } = this.state;
    const modifiedOwner = Object.assign({}, owner, { [name]: value });
    const newFieldErrors = error ? Object.assign({}, error.fieldErrors, {[name]: fieldError }) : {[name]: fieldError };
    this.setState({
      owner: modifiedOwner,
      error: { fieldErrors: newFieldErrors }
    });
  }

  render() {
    const { owner, error } = this.state;
    return (
      <span>
        <h2>{owner.isNew ? '飼い主の新規登録' : '飼い主の編集'}</h2>
        <form className='form-horizontal' method='POST' action={url('/api/owner')}>
          <div className='form-group has-feedback'>
            <Input object={owner} error={error} constraint={NotEmpty} label='名' name='firstName' onChange={this.onInputChange} />
            <Input object={owner} error={error} constraint={NotEmpty} label='姓' name='lastName' onChange={this.onInputChange} />
            <Input object={owner} error={error} constraint={NotEmpty} label='住所' name='address' onChange={this.onInputChange} />
            <Input object={owner} error={error} constraint={NotEmpty} label='市区町村' name='city' onChange={this.onInputChange} />
            <Input object={owner} error={error} constraint={Digits(11)} label='電話番号' name='telephone' onChange={this.onInputChange} />
          </div>
          <div className='form-group'>
            <div className='col-sm-offset-2 col-sm-10'>
              <button className='btn btn-default' type='submit' onClick={this.onSubmit}>{owner.isNew ? '登録' : '更新'}</button>
            </div>
          </div>
        </form>
      </span>
    );
  }
}
