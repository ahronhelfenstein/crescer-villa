import { Moment } from 'moment';

export interface IEventos {
  id?: number;
  nome?: string;
  urlImagem?: string;
  data?: Moment;
}

export const defaultValue: Readonly<IEventos> = {};
