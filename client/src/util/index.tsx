import { IHttpMethod } from '../types';

declare var __API_SERVER_URL__;
const BACKEND_URL = (typeof __API_SERVER_URL__ === 'undefined' ? 'http://localhost:9966/petclinic' : __API_SERVER_URL__);

export const url = (path: string): string => {
  const base = BACKEND_URL.replace(/\/+$/, '');
  const relative = path.replace(/^\/+/, '');
  return `${base}/${relative}`;
};

const fieldErrorsFromHeader = (response: Response) => {
  if (!response.headers || !response.headers.get) {
    return null;
  }
  const header = response.headers.get('errors');
  if (!header) {
    return null;
  }
  try {
    const parsed = JSON.parse(header);
    if (!Array.isArray(parsed)) {
      return null;
    }
    const fieldErrors = {};
    parsed.forEach(item => {
      if (item && item.fieldName) {
        fieldErrors[item.fieldName] = { field: item.fieldName, message: item.errorMessage };
      }
    });
    return { fieldErrors };
  } catch (e) {
    return null;
  }
};

/**
 * path: relative PATH without host and port (i.e. '/api/123')
 * data: object that will be passed as request body
 * onSuccess: callback handler if request succeeded. Succeeded means it could technically be handled (i.e. valid json is returned)
 * regardless of the HTTP status code.
 */
export const submitForm = (method: IHttpMethod, path: string, data: any, onSuccess: (status: number, response: any) => void) => {
  const requestUrl = url(path);

  const fetchParams = {
    method: method,
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  };

  console.log('Submitting to ' + method + ' ' + requestUrl);
  return fetch(requestUrl, fetchParams)
    .then(response => {
      if (response.status === 204) {
        onSuccess(response.status, fieldErrorsFromHeader(response) || {});
        return;
      }
      return response.text().then(text => {
        let body = {};
        if (text) {
          try {
            body = JSON.parse(text);
          } catch (e) {
            body = { message: text };
          }
        }
        onSuccess(response.status, fieldErrorsFromHeader(response) || body);
      });
    });
};
