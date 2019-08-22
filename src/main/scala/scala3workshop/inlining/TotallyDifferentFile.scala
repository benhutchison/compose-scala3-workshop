package scala3workshop.inlining

object TotallyDifferentFile {
  def somethingElse(): Unit = {
    Logger.log("Whoa!")(1 + 1)
  }
}