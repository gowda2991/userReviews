import com.google.inject.AbstractModule
import modules.{DBSetup, UserLevelsSetup}

class Module extends AbstractModule {
  override def configure(): Unit = {
    install(new DBSetup)
    install(new UserLevelsSetup)
  }

}