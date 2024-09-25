import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';

export interface INotification {
  id?: number;
  message?: string;
  status?: string | null;
  createdAt?: dayjs.Dayjs | null;
  user?: IUser | null;
}

export const defaultValue: Readonly<INotification> = {};
