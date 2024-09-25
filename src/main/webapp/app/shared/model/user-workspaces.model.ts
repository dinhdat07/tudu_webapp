import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IWorkspace } from 'app/shared/model/workspace.model';

export interface IUserWorkspaces {
  id?: number;
  privilege?: string | null;
  createdAt?: dayjs.Dayjs | null;
  user?: IUser | null;
  workspace?: IWorkspace | null;
}

export const defaultValue: Readonly<IUserWorkspaces> = {};
