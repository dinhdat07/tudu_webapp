import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntities as getWorkspaces } from 'app/entities/workspace/workspace.reducer';
import { createEntity, getEntity, reset, updateEntity } from './user-workspaces.reducer';

export const UserWorkspacesUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const workspaces = useAppSelector(state => state.workspace.entities);
  const userWorkspacesEntity = useAppSelector(state => state.userWorkspaces.entity);
  const loading = useAppSelector(state => state.userWorkspaces.loading);
  const updating = useAppSelector(state => state.userWorkspaces.updating);
  const updateSuccess = useAppSelector(state => state.userWorkspaces.updateSuccess);

  const handleClose = () => {
    navigate(`/user-workspaces${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getWorkspaces({}));
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
    values.createdAt = convertDateTimeToServer(values.createdAt);

    const entity = {
      ...userWorkspacesEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user?.toString()),
      workspace: workspaces.find(it => it.id.toString() === values.workspace?.toString()),
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
          createdAt: displayDefaultDateTime(),
        }
      : {
          ...userWorkspacesEntity,
          createdAt: convertDateTimeFromServer(userWorkspacesEntity.createdAt),
          user: userWorkspacesEntity?.user?.id,
          workspace: userWorkspacesEntity?.workspace?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="tuduApp.userWorkspaces.home.createOrEditLabel" data-cy="UserWorkspacesCreateUpdateHeading">
            <Translate contentKey="tuduApp.userWorkspaces.home.createOrEditLabel">Create or edit a UserWorkspaces</Translate>
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
                  id="user-workspaces-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('tuduApp.userWorkspaces.privilege')}
                id="user-workspaces-privilege"
                name="privilege"
                data-cy="privilege"
                type="text"
              />
              <ValidatedField
                label={translate('tuduApp.userWorkspaces.createdAt')}
                id="user-workspaces-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="user-workspaces-user"
                name="user"
                data-cy="user"
                label={translate('tuduApp.userWorkspaces.user')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="user-workspaces-workspace"
                name="workspace"
                data-cy="workspace"
                label={translate('tuduApp.userWorkspaces.workspace')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/user-workspaces" replace color="info">
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

export default UserWorkspacesUpdate;
