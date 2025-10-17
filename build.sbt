/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
Global / majorVersion := 1
Global / scalaVersion := "3.3.6"

lazy val microservice = Project("digital-contact-stub", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    pipelineStages := Seq(gzip)
  )
  .settings(CodeCoverageSettings.settings *)
  .settings(
    scalacOptions ++= List(
      // Silence unused imports in template files
      "-Wconf:msg=unused import&src=.*:s",
      // Silence "Flag -XXX set repeatedly"
      "-Wconf:msg=Flag.*repeatedly:s",
      // Silence unused warnings on Play `routes` files
      "-Wconf:src=routes/.*:s"
    )
  )

Test / test := (Test / test)
  .dependsOn(scalafmtCheckAll)
  .value
