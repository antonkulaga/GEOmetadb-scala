package model.persistence

import java.util.UUID
import scala.language.implicitConversions
import com.micronautics.HasValue


protected sealed class IdType[+T](val emptyValue: T)

protected object IdType {
  val emptyDouble = 0.0
  val emptyLong = 0L
  val emptyUuid = new UUID(0L, 0L)

  def apply[T](implicit idType: IdType[T]): IdType[T] = idType
  implicit object LongWitness   extends IdType[Long](emptyLong)
  implicit object DoubleWitness   extends IdType[Double](emptyDouble)
  implicit object StringWitness extends IdType[String]("")
  implicit object UUIDWitness   extends IdType[UUID](emptyUuid)


  // delegates to other IdTypes
  implicit def OptionWitness[T]( implicit contained: IdType[T] ): IdType[Option[T]]
  = new IdType[Option[T]](None)
}

protected sealed class IdConverter[From, To: IdType](val convertValue: From => To)

protected object IdConverter {
  implicit def id[T: IdType]: IdConverter[T, T] = new IdConverter[T, T](identity)

  implicit def option[From, To: IdType](
                                         implicit valueConverter: IdConverter[From, To]
                                       ): IdConverter[From, Option[To]] = new IdConverter[From, Option[To]](
    value => Some(valueConverter.convertValue(value))
  )

  implicit object StringLong extends IdConverter[String, Long](_.toLong)
  implicit object StringUUID extends IdConverter[String, UUID](UUID.fromString)
  implicit object LongString extends IdConverter[Long, String](_.toString)
  implicit object LongUUID   extends IdConverter[Long, UUID](long => UUID.fromString(long.toString))
}

/** To use, either import `IdImplicits._` or mix in IdImplicitLike */
trait IdImplicitLike {
  implicit class ToId[From](from: From) {
    def toId[To: IdType](implicit converter: IdConverter[From, To]) = Id(converter.convertValue(from))
  }

  implicit def idOptionLongToBigDecimal(id: Id[Option[Long]]): BigDecimal =
    BigDecimal(id.value.getOrElse(Id.empty[Long].value))

  implicit def idLongToBigDecimal(id: Id[Long]): BigDecimal = BigDecimal(id.value)


  implicit def idOptionDoubleToBigDecimal(id: Id[Option[Double]]): BigDecimal =
    BigDecimal(id.value.getOrElse(Id.empty[Double].value))

  implicit def idDoubleToBigDecimal(id: Id[Double]): BigDecimal = BigDecimal(id.value)

}

object IdImplicits extends IdImplicitLike

object Id extends IdImplicitLike {
  def empty[T](implicit idType: IdType[T]): Id[T] = Id(idType.emptyValue)

  def isEmpty[T](id: Id[T])(implicit idType: IdType[T]): Boolean = id.value == idType.emptyValue

  def isValid[T: IdType](value: T): Boolean = try {
    Id(value)
    true
  } catch {
    case _: Exception => false
  }
}

case class Id[T: IdType](value: T) extends HasValue[T] {
  /*def fromOption: Id[_ >: UUID with Long with T] = this.value match {
    case v: Option[_] if v==None =>
      this match {
        case id: [T =:= Id[Long]] => Id(IdType.emptyLong)
        /*case id
        else if (v.contains(IdType.emptyUuid)) Id(IdType.emptyUuid)
          else this*/
      }
    case v: Option[UUID] if v==None =>
      Id(IdType.emptyUuid)
    case v: Option[UUID] if v.isInstanceOf[Some[_]] =>
      if (v.contains(IdType.emptyUuid)) Id.empty[UUID] else Id(v.get)
    case _ => this
  }*/

  def toOption: Id[_ >: T with Option[T]] = this.value match {
    case v if v.isInstanceOf[Option[_]] =>
      this

    case v if v.isInstanceOf[UUID] =>
      if (v==IdType.emptyUuid) Id.empty[Option[T]] else Id(Option(v))

    case v if v.isInstanceOf[Long] =>
      if (v==IdType.emptyLong) Id.empty[Option[T]] else Id(Option(v))
  }

  override def toString: String = value match {
    case Some(x) => x.toString

    //case None => "" // Scala compiler does not like this, so the following craziness is used:
    case n if n == None => ""

    case x => x.toString
  }
}

trait HasId[T, A] extends IdImplicitLike {
  def id: Id[A]

  def setId(newId: Id[A]): T = Copier.apply[T](this.asInstanceOf[T], "id" -> newId)

  //  def clearId: T = setId(Id.empty[A]) // TODO how to make this work?
}