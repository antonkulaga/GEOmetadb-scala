package group.research.aging.util


import shapeless.Poly1

object NotNull extends Poly1 {

  implicit val intCase: Case.Aux[Int, Int] =
    at[Int](v => v)

  implicit val longCase: Case.Aux[Long, Long] =
    at[Long](v => v)


  implicit val doubleCase: Case.Aux[Double, Double] =
    at[Double](v => if(v == Double.NaN) 0.0 else v)

  implicit val stringCase: Case.Aux[String, String] =
    at[String](v => if(v==null || v == "null") "" else v)

}