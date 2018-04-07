package group.research.aging.geometa.web.actions

import io.circe.generic.JsonCodec

trait UpdateUI extends Action

@JsonCodec case class EvalJS(str: String) extends UpdateUI

case object NotUpdateUI extends UpdateUI