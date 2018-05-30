import $exec.dependencies
import dependencies._

import group.research.aging.geometa.models.Sequencing
import group.research.aging.geometa.web.actions.{QueryParameters, SuggestionsInfo}
import group.research.aging.geometa.web.controller.Controller
//import group.research.aging.geometa.models._


println("starting")

import scala.collection.immutable._
//import wvlet.log.{LogLevel, LogSupport, Logger}

import cats.effect.IO
import doobie.hikari._



class TestController(transactor: IO[HikariTransactor[IO]]) extends Controller(transactor){

  def sequencingQuery(species: List[String] = Nil,
                      molecules: List[String] = Nil,
                      sequencers: List[String] = Nil,
                      andLikeCharacteristics: List[String] = Nil,
                      orLikeCharacteristics: List[String] = Nil,
                      limit: Int = 0, offset: Int = 0) = {
    val where =  makeWhere(species, molecules, sequencers, andLikeCharacteristics, orLikeCharacteristics)
    (sampleSelection ++ where ++ limitation(limit, offset)).query[Sequencing]
  }

  def loadSequencingQuery( parameters: QueryParameters) = {
    sequencingQuery(species = parameters.species,
      molecules = parameters.molecules,
      sequencers = parameters.sequencers,
      andLikeCharacteristics = parameters.andLikeCharacteristics,
      orLikeCharacteristics = parameters.orLikeCharacteristics,
      limit = parameters.limit,
      offset = parameters.offset)
  }

  /*
  def loadSequencing(
                      parameters: QueryParameters
                    ) = {
    val gsms = super.sequencing(species = parameters.species,
      molecules = parameters.molecules,
      sequencers = parameters.sequencers,
      andLikeCharacteristics = parameters.andLikeCharacteristics,
      orLikeCharacteristics = parameters.orLikeCharacteristics,
      limit = parameters.limit,
      offset = parameters.offset)
    val suggestions = SuggestionsInfo(super.all_species().toList, super.all_sequencers().toList, super.all_molecules().toList) //TODO: fix collections
    LoadedSequencing(suggestions, parameters, gsms)
    //actions
  }
  */

}

println("Classes Loaded")