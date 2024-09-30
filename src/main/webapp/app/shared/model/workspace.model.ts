import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { Privilege } from 'app/shared/model/enumerations/privilege.model';

export interface IWorkspace {
  id?: number;
  name?: string;
  description?: string | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  privilege?: keyof typeof Privilege | null;
  users?: IUser[] | null;
}

export const defaultValue: Readonly<IWorkspace> = {};
