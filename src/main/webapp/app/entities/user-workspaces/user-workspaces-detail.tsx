import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './user-workspaces.reducer';

export const UserWorkspacesDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const userWorkspacesEntity = useAppSelector(state => state.userWorkspaces.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="userWorkspacesDetailsHeading">
          <Translate contentKey="tuduApp.userWorkspaces.detail.title">UserWorkspaces</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{userWorkspacesEntity.id}</dd>
          <dt>
            <span id="privilege">
              <Translate contentKey="tuduApp.userWorkspaces.privilege">Privilege</Translate>
            </span>
          </dt>
          <dd>{userWorkspacesEntity.privilege}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="tuduApp.userWorkspaces.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {userWorkspacesEntity.createdAt ? (
              <TextFormat value={userWorkspacesEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="tuduApp.userWorkspaces.user">User</Translate>
          </dt>
          <dd>{userWorkspacesEntity.user ? userWorkspacesEntity.user.id : ''}</dd>
          <dt>
            <Translate contentKey="tuduApp.userWorkspaces.workspace">Workspace</Translate>
          </dt>
          <dd>{userWorkspacesEntity.workspace ? userWorkspacesEntity.workspace.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/user-workspaces" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/user-workspaces/${userWorkspacesEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default UserWorkspacesDetail;
