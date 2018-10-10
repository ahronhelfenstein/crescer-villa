import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IEventos, defaultValue } from 'app/shared/model/eventos.model';

export const ACTION_TYPES = {
  FETCH_EVENTOS_LIST: 'eventos/FETCH_EVENTOS_LIST',
  FETCH_EVENTOS: 'eventos/FETCH_EVENTOS',
  CREATE_EVENTOS: 'eventos/CREATE_EVENTOS',
  UPDATE_EVENTOS: 'eventos/UPDATE_EVENTOS',
  DELETE_EVENTOS: 'eventos/DELETE_EVENTOS',
  RESET: 'eventos/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IEventos>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false
};

export type EventosState = Readonly<typeof initialState>;

// Reducer

export default (state: EventosState = initialState, action): EventosState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_EVENTOS_LIST):
    case REQUEST(ACTION_TYPES.FETCH_EVENTOS):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_EVENTOS):
    case REQUEST(ACTION_TYPES.UPDATE_EVENTOS):
    case REQUEST(ACTION_TYPES.DELETE_EVENTOS):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.FETCH_EVENTOS_LIST):
    case FAILURE(ACTION_TYPES.FETCH_EVENTOS):
    case FAILURE(ACTION_TYPES.CREATE_EVENTOS):
    case FAILURE(ACTION_TYPES.UPDATE_EVENTOS):
    case FAILURE(ACTION_TYPES.DELETE_EVENTOS):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_EVENTOS_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_EVENTOS):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_EVENTOS):
    case SUCCESS(ACTION_TYPES.UPDATE_EVENTOS):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_EVENTOS):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {}
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

const apiUrl = 'api/eventos';

// Actions

export const getEntities: ICrudGetAllAction<IEventos> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_EVENTOS_LIST,
  payload: axios.get<IEventos>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<IEventos> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_EVENTOS,
    payload: axios.get<IEventos>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IEventos> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_EVENTOS,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IEventos> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_EVENTOS,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IEventos> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_EVENTOS,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
