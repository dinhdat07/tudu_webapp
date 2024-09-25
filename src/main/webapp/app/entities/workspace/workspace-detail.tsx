import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './workspace.reducer';

export const WorkspaceDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const workspaceEntity = useAppSelector(state => state.workspace.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="workspaceDetailsHeading">
          <Translate contentKey="tuduApp.workspace.detail.title">Workspace</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{workspaceEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="tuduApp.workspace.name">Name</Translate>
            </span>
          </dt>
          <dd>{workspaceEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="tuduApp.workspace.description">Description</Translate>
            </span>
          </dt>
          <dd>{workspaceEntity.description}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="tuduApp.workspace.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {workspaceEntity.createdAt ? <TextFormat value={workspaceEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="tuduApp.workspace.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {workspaceEntity.updatedAt ? <TextFormat value={workspaceEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
        </dl>
        <Button tag={Link} to="/workspace" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/workspace/${workspaceEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default WorkspaceDetail;
