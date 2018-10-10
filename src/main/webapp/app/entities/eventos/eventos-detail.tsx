import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './eventos.reducer';
import { IEventos } from 'app/shared/model/eventos.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IEventosDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class EventosDetail extends React.Component<IEventosDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { eventosEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            Eventos [<b>{eventosEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="nome">Nome</span>
            </dt>
            <dd>{eventosEntity.nome}</dd>
            <dt>
              <span id="urlImagem">Url Imagem</span>
            </dt>
            <dd>{eventosEntity.urlImagem}</dd>
            <dt>
              <span id="data">Data</span>
            </dt>
            <dd>
              <TextFormat value={eventosEntity.data} type="date" format={APP_DATE_FORMAT} />
            </dd>
          </dl>
          <Button tag={Link} to="/entity/eventos" replace color="info">
            <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/eventos/${eventosEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
          </Button>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = ({ eventos }: IRootState) => ({
  eventosEntity: eventos.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(EventosDetail);
