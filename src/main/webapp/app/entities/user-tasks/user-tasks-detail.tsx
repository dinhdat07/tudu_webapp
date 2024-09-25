import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './user-tasks.reducer';

export const UserTasksDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const userTasksEntity = useAppSelector(state => state.userTasks.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="userTasksDetailsHeading">
          <Translate contentKey="tuduApp.userTasks.detail.title">UserTasks</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{userTasksEntity.id}</dd>
          <dt>
            <span id="privilege">
              <Translate contentKey="tuduApp.userTasks.privilege">Privilege</Translate>
            </span>
          </dt>
          <dd>{userTasksEntity.privilege}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="tuduApp.userTasks.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {userTasksEntity.createdAt ? <TextFormat value={userTasksEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="tuduApp.userTasks.user">User</Translate>
          </dt>
          <dd>{userTasksEntity.user ? userTasksEntity.user.id : ''}</dd>
          <dt>
            <Translate contentKey="tuduApp.userTasks.task">Task</Translate>
          </dt>
          <dd>{userTasksEntity.task ? userTasksEntity.task.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/user-tasks" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/user-tasks/${userTasksEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default UserTasksDetail;
