package group.research.aging.util

import java.net.{URLDecoder, URLEncoder}

import scala.util.matching.Regex

/**
  * Andreas Neumann
  * Email: andreas@neumann.biz
  * Date: 08.07.16
  * Time: 13:22
  */
object PercentDecoder {

  implicit val encoding: String = "utf-8"
  val whitespacePlus: Regex = "\\+".r
  val whiteSpaceEscape: String = "%20"

  implicit class DecodedPercentString(val unencoded: String) extends AnyVal{
    def encode(implicit encoding: String) =  PercentDecoder.PercentEncodedString(
      whitespacePlus replaceAllIn( URLEncoder.encode(unencoded, encoding), whiteSpaceEscape)
    )
    override def toString = unencoded
  }

  implicit class PercentEncodedString(val encodedString: String) extends AnyVal{
    def decode(implicit encoding: String): String = URLDecoder.decode(encodedString, encoding)
    override def toString = encodedString
  }

  implicit def decodedString(encoded: PercentEncodedString) : String = encoded.decode

}