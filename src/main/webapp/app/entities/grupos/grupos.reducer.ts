import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IGrupos, defaultValue } from 'app/shared/model/grupos.model';

export const ACTION_TYPES = {
  FETCH_GRUPOS_LIST: 'grupos/FETCH_GRUPOS_LIST',
  FETCH_GRUPOS: 'grupos/FETCH_GRUPOS',
  CREATE_GRUPOS: 'grupos/CREATE_GRUPOS',
  UPDATE_GRUPOS: 'grupos/UPDATE_GRUPOS',
  DELETE_GRUPOS: 'grupos/DELETE_GRUPOS',
  RESET: 'grupos/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IGrupos>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false
};

export type GruposState = Readonly<typeof initialState>;

// Reducer

export default (state: GruposState = initialState, action): GruposState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_GRUPOS_LIST):
    case REQUEST(ACTION_TYPES.FETCH_GRUPOS):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_GRUPOS):
    case REQUEST(ACTION_TYPES.UPDATE_GRUPOS):
    case REQUEST(ACTION_TYPES.DELETE_GRUPOS):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.FETCH_GRUPOS_LIST):
    case FAILURE(ACTION_TYPES.FETCH_GRUPOS):
    case FAILURE(ACTION_TYPES.CREATE_GRUPOS):
    case FAILURE(ACTION_TYPES.UPDATE_GRUPOS):
    case FAILURE(ACTION_TYPES.DELETE_GRUPOS):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_GRUPOS_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_GRUPOS):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_GRUPOS):
    case SUCCESS(ACTION_TYPES.UPDATE_GRUPOS):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_GRUPOS):
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

const apiUrl = 'api/grupos';

// Actions

export const getEntities: ICrudGetAllAction<IGrupos> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_GRUPOS_LIST,
  payload: axios.get<IGrupos>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<IGrupos> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_GRUPOS,
    payload: axios.get<IGrupos>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IGrupos> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_GRUPOS,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IGrupos> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_GRUPOS,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<IGrupos> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_GRUPOS,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
