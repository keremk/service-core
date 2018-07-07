package com.xing.jobs.servicecore.config

import org.scalatest._

class ConfigUtilsSpec extends WordSpec with Matchers {
  class MockSystem extends SystemGettable {
    override def getEnvVar(name: String): Option[String] = name match {
      case "MATCH" => Some("Matched")
      case "NOMATCH" => None
    }

    override def getPropsVar(name: String): Option[String] = name match {
      case "java.home" => Some("home")
      case "java.nomatch" => None
    }
  }

  object ConfigUtilsSUT extends ConfigUtils
  implicit val system: MockSystem = new MockSystem()

  "envVar call" should {
    "match and return value if environment variable exists" in {
      val value: Option[String] = ConfigUtilsSUT.envVar("MATCH")
      value should be(Some("Matched"))
    }
    "not match and return None if environment variable does not exist" in {
      val value: Option[String] = ConfigUtilsSUT.envVar("NOMATCH")
      value should be(None)
    }
  }
  "envVarOrDefault call" should {
    "return the environment variable if it exists" in {
      val value = ConfigUtilsSUT.envVarOrDefault("MATCH", "Default")
      value should be ("Matched")
    }
    "return the default value if environment variable does not exist" in {
      val value = ConfigUtilsSUT.envVarOrDefault("NOMATCH", "Default")
      value should be ("Default")
    }
  }
  "envVarOrFail call" should {
    "return the environment variable if it exists" in {
      val value = ConfigUtilsSUT.envVarOrFail("MATCH")
      value should be ("Matched")
    }
    "throw a runtime exception if environment variable does not exist" in {
      val thrown = the [RuntimeException] thrownBy ConfigUtilsSUT.envVarOrFail("NOMATCH")
      thrown.getMessage should equal ("Required environment variable 'NOMATCH' not found")
    }
  }
  "propertyVar call" should {
    "match and return value if environment variable exists" in {
      val value: Option[String] = ConfigUtilsSUT.propertyVar("java.home")
      value should be(Some("home"))
    }
    "not match and return None if environment variable does not exist" in {
      val value: Option[String] = ConfigUtilsSUT.propertyVar("java.nomatch")
      value should be(None)
    }
  }
  "propertyVarOrFail call" should {
    "return the environment variable if it exists" in {
      val value = ConfigUtilsSUT.propertyVarOrFail("java.home")
      value should be ("home")
    }
    "throw a runtime exception if environment variable does not exist" in {
      val thrown = the [RuntimeException] thrownBy ConfigUtilsSUT.propertyVarOrFail("java.nomatch")
      thrown.getMessage should equal ("Required system property 'java.nomatch' not found")
    }
  }
}
