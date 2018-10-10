import React from 'react';
import { Switch } from 'react-router-dom';

// tslint:disable-next-line:no-unused-variable
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Post from './post';
import Perfil from './perfil';
import Grupos from './grupos';
import Eventos from './eventos';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}/post`} component={Post} />
      <ErrorBoundaryRoute path={`${match.url}/perfil`} component={Perfil} />
      <ErrorBoundaryRoute path={`${match.url}/grupos`} component={Grupos} />
      <ErrorBoundaryRoute path={`${match.url}/eventos`} component={Eventos} />
      {/* jhipster-needle-add-route-path - JHipster will routes here */}
    </Switch>
  </div>
);

export default Routes;
