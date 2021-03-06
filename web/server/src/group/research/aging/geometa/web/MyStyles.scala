package group.research.aging.geometa.web

import scalacss.DevDefaults._

object MyStyles extends StyleSheet.Standalone {

  import dsl._

  media.maxWidth(1024 px) - {
    &("html") - {
      fontSize(8 pt)
    }
  }
  media.minWidth(1281 px) - {
    &("html") - {
      fontSize(12 pt)
    }
  }

  "body" - (
//    backgroundColor(skyblue)
    )

  "#main" - (
    overflowX.scroll,
  )

  "#cromwell" - (
    margin(20 px),
    )

  "#url" - (
    minWidth(330 px)
  )
}