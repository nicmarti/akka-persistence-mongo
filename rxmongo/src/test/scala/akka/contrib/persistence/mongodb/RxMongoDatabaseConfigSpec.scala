package akka.contrib.persistence.mongodb

import akka.contrib.persistence.mongodb.ConfigLoanFixture._
import com.typesafe.config.ConfigFactory
import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner
import org.scalatest.time.{Millis, Seconds, Span}

@RunWith(classOf[JUnitRunner])
class RxMongoDatabaseConfigSpec extends BaseUnitTest with ContainerMongo with BeforeAndAfterAll with ScalaFutures {

  override def afterAll(): Unit = {
    cleanup()
  }

  val config = ConfigFactory.parseString(
    s"""|akka.contrib.persistence.mongodb.mongo {
        | mongouri = "mongodb://$host:$noAuthPort/$embedDB"
        | database = "User-Specified-Database"
        |}
      """.stripMargin)

  override def embedDB = "admin"

  "Persistence store database config" should "be User-Specified-Database" in withConfig(config, "akka-contrib-mongodb-persistence-journal") { case (actorSystem, c) =>
    val underTest = new RxMongoDriver(actorSystem, c, new RxMongoDriverProvider(actorSystem))
    whenReady(underTest.database, timeout(Span(6, Seconds)), interval(Span(500, Millis))) { db =>
      db.name should be("User-Specified-Database")
    }
  }

}
