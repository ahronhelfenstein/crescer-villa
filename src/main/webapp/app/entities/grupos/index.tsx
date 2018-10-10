import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Grupos from './grupos';
import GruposDetail from './grupos-detail';
import GruposUpdate from './grupos-update';
import GruposDeleteDialog from './grupos-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={GruposUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={GruposUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={GruposDetail} />
      <ErrorBoundaryRoute path={match.url} component={Grupos} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={GruposDeleteDialog} />
  </>
);

export default Routes;
