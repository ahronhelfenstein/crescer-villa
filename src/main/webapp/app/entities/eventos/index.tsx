import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Eventos from './eventos';
import EventosDetail from './eventos-detail';
import EventosUpdate from './eventos-update';
import EventosDeleteDialog from './eventos-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={EventosUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={EventosUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={EventosDetail} />
      <ErrorBoundaryRoute path={match.url} component={Eventos} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={EventosDeleteDialog} />
  </>
);

export default Routes;
