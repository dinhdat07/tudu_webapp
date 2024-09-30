import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './task.reducer';

export const TaskDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const taskEntity = useAppSelector(state => state.task.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="taskDetailsHeading">
          <Translate contentKey="tuduApp.task.detail.title">Task</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{taskEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="tuduApp.task.title">Title</Translate>
            </span>
          </dt>
          <dd>{taskEntity.title}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="tuduApp.task.description">Description</Translate>
            </span>
          </dt>
          <dd>{taskEntity.description}</dd>
          <dt>
            <span id="dueDate">
              <Translate contentKey="tuduApp.task.dueDate">Due Date</Translate>
            </span>
          </dt>
          <dd>{taskEntity.dueDate ? <TextFormat value={taskEntity.dueDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="priority">
              <Translate contentKey="tuduApp.task.priority">Priority</Translate>
            </span>
          </dt>
          <dd>{taskEntity.priority}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="tuduApp.task.status">Status</Translate>
            </span>
          </dt>
          <dd>{taskEntity.status}</dd>
          <dt>
            <span id="category">
              <Translate contentKey="tuduApp.task.category">Category</Translate>
            </span>
          </dt>
          <dd>{taskEntity.category}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="tuduApp.task.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{taskEntity.createdAt ? <TextFormat value={taskEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="tuduApp.task.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{taskEntity.updatedAt ? <TextFormat value={taskEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="privilege">
              <Translate contentKey="tuduApp.task.privilege">Privilege</Translate>
            </span>
          </dt>
          <dd>{taskEntity.privilege}</dd>
          <dt>
            <Translate contentKey="tuduApp.task.workspace">Workspace</Translate>
          </dt>
          <dd>{taskEntity.workspace ? taskEntity.workspace.id : ''}</dd>
          <dt>
            <Translate contentKey="tuduApp.task.user">User</Translate>
          </dt>
          <dd>
            {taskEntity.users
              ? taskEntity.users.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {taskEntity.users && i === taskEntity.users.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/task" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/task/${taskEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TaskDetail;
