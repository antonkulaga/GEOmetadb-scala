/*
package group.research.aging.geometa.web.model

trait SelectedCtx extends model.persistence.SqliteCtx

import group.research.aging.geometa.Tables
import io.getquill.context.jdbc.JdbcContext
import model.persistence.QuillCacheImplicits

trait MyQuillImplicits { ctx: JdbcContext[_, _] =>
  // define Quill Decoders, Encoders and Mappers here
}

case object Ctx extends SelectedCtx with QuillCacheImplicits with MyQuillImplicits

import model.persistence._

object Samples extends CachedPersistence[Long, Option[Long], Tables.gsm]
  with StrongCacheLike[Long, Option[Long], Tables.gsm] {
  import Ctx._
  @inline def _findAll: List[Tables.gsm] = run { quote { query[Tables.gsm] } }

  val queryById: IdOptionLong => Quoted[EntityQuery[Tables.gsm]] =
    (id: IdOptionLong) =>
      quote { query[Tables.gsm].filter(_.id == lift(id)) }

  val _deleteById: (IdOptionLong) => Unit =
    (id: IdOptionLong) => {
      run { quote { queryById(id).delete } }
      ()
    }

  val _findById: IdOptionLong => Option[Tables.gsm] =
    (id: Id[Option[Long]]) =>
      run { quote { queryById(id) } }.headOption

  val _insert: Tables.gsm => Tables.gsm =
    (Tables.gsm: Tables.gsm) => {
      val id: Id[Option[Long]] = try {
        run { quote { query[Tables.gsm].insert(lift(Tables.gsm)) }.returning(_.id) }
      } catch {
        case e: Throwable =>
          logger.error(e.getMessage)
          throw e
      }
      Tables.gsm.setId(id)
    }

  val _update: Tables.gsm => Tables.gsm =
    (Tables.gsm: Tables.gsm) => {
      run { queryById(Tables.gsm.id).update(lift(Tables.gsm)) }
      Tables.gsm
    }
}
*/