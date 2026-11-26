import { IConstraint } from '../../types';

export const NotEmpty: IConstraint = {
  message:    '1文字以上入力してください',
  validate: (value) => {
    return !!value && value.length > 0;
  }
};

export const Digits = (digits: number): IConstraint => {
  const reg = new RegExp('^\\d{1,' + digits + '}$');
  return {
    message: digits + '桁以内の数字で入力してください',
    validate: (value) => {
      return !!value && value.match(reg) !== null;
    }
  };
};
