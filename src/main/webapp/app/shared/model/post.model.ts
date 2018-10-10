export interface IPost {
  id?: number;
  titulo?: string;
  descricao?: string;
  urlImagem?: string;
}

export const defaultValue: Readonly<IPost> = {};
