import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { Privilege } from 'app/shared/model/enumerations/privilege.model';
import { createEntity, getEntity, reset, updateEntity } from './workspace.reducer';

export const WorkspaceUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const workspaceEntity = useAppSelector(state => state.workspace.entity);
  const loading = useAppSelector(state => state.workspace.loading);
  const updating = useAppSelector(state => state.workspace.updating);
  const updateSuccess = useAppSelector(state => state.workspace.updateSuccess);
  const privilegeValues = Object.keys(Privilege);

  const handleClose = () => {
    navigate(`/workspace${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

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
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...workspaceEntity,
      ...values,
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
          createdAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
        }
      : {
          privilege: 'VIEW',
          ...workspaceEntity,
          createdAt: convertDateTimeFromServer(workspaceEntity.createdAt),
          updatedAt: convertDateTimeFromServer(workspaceEntity.updatedAt),
          users: workspaceEntity?.users?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="tuduApp.workspace.home.createOrEditLabel" data-cy="WorkspaceCreateUpdateHeading">
            <Translate contentKey="tuduApp.workspace.home.createOrEditLabel">Create or edit a Workspace</Translate>
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
                  id="workspace-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('tuduApp.workspace.name')}
                id="workspace-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('tuduApp.workspace.description')}
                id="workspace-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                label={translate('tuduApp.workspace.createdAt')}
                id="workspace-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('tuduApp.workspace.updatedAt')}
                id="workspace-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('tuduApp.workspace.privilege')}
                id="workspace-privilege"
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
                label={translate('tuduApp.workspace.user')}
                id="workspace-user"
                data-cy="user"
                type="select"
                multiple
                name="users"
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/workspace" replace color="info">
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

export default WorkspaceUpdate;
