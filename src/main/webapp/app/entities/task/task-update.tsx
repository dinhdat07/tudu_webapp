import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getWorkspaces } from 'app/entities/workspace/workspace.reducer';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { Priority } from 'app/shared/model/enumerations/priority.model';
import { Status } from 'app/shared/model/enumerations/status.model';
import { Privilege } from 'app/shared/model/enumerations/privilege.model';
import { createEntity, getEntity, reset, updateEntity } from './task.reducer';

export const TaskUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const workspaces = useAppSelector(state => state.workspace.entities);
  const users = useAppSelector(state => state.userManagement.users);
  const taskEntity = useAppSelector(state => state.task.entity);
  const loading = useAppSelector(state => state.task.loading);
  const updating = useAppSelector(state => state.task.updating);
  const updateSuccess = useAppSelector(state => state.task.updateSuccess);
  const priorityValues = Object.keys(Priority);
  const statusValues = Object.keys(Status);
  const privilegeValues = Object.keys(Privilege);

  const handleClose = () => {
    navigate(`/task${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getWorkspaces({}));
    dispatch(getUsers({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.dueDate = convertDateTimeToServer(values.dueDate);
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...taskEntity,
      ...values,
      workspace: workspaces.find(it => it.id.toString() === values.workspace?.toString()),
      users: mapIdList(values.users),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          dueDate: displayDefaultDateTime(),
          createdAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
        }
      : {
          priority: 'LOW',
          status: 'PENDING',
          privilege: 'VIEW',
          ...taskEntity,
          dueDate: convertDateTimeFromServer(taskEntity.dueDate),
          createdAt: convertDateTimeFromServer(taskEntity.createdAt),
          updatedAt: convertDateTimeFromServer(taskEntity.updatedAt),
          workspace: taskEntity?.workspace?.id,
          users: taskEntity?.users?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="tuduApp.task.home.createOrEditLabel" data-cy="TaskCreateUpdateHeading">
            <Translate contentKey="tuduApp.task.home.createOrEditLabel">Create or edit a Task</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="task-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('tuduApp.task.title')}
                id="task-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('tuduApp.task.description')}
                id="task-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                label={translate('tuduApp.task.dueDate')}
                id="task-dueDate"
                name="dueDate"
                data-cy="dueDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('tuduApp.task.priority')}
                id="task-priority"
                name="priority"
                data-cy="priority"
                type="select"
              >
                {priorityValues.map(priority => (
                  <option value={priority} key={priority}>
                    {translate(`tuduApp.Priority.${priority}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField label={translate('tuduApp.task.status')} id="task-status" name="status" data-cy="status" type="select">
                {statusValues.map(status => (
                  <option value={status} key={status}>
                    {translate(`tuduApp.Status.${status}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('tuduApp.task.category')}
                id="task-category"
                name="category"
                data-cy="category"
                type="text"
              />
              <ValidatedField
                label={translate('tuduApp.task.createdAt')}
                id="task-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('tuduApp.task.updatedAt')}
                id="task-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('tuduApp.task.privilege')}
                id="task-privilege"
                name="privilege"
                data-cy="privilege"
                type="select"
              >
                {privilegeValues.map(privilege => (
                  <option value={privilege} key={privilege}>
                    {translate(`tuduApp.Privilege.${privilege}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="task-workspace"
                name="workspace"
                data-cy="workspace"
                label={translate('tuduApp.task.workspace')}
                type="select"
              >
                <option value="" key="0" />
                {workspaces
                  ? workspaces.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField label={translate('tuduApp.task.user')} id="task-user" data-cy="user" type="select" multiple name="users">
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/task" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default TaskUpdate;
