package group.research.aging.geometa.web.model

//import model.dao.SelectedCtx
import model.persistence.QuillCacheImplicits


trait SelectedCtx extends model.persistence.H2Ctx

case object Ctx extends SelectedCtx with QuillCacheImplicits