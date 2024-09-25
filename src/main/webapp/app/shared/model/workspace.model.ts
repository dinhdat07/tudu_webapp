import dayjs from 'dayjs';

export interface IWorkspace {
  id?: number;
  name?: string;
  description?: string | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<IWorkspace> = {};
