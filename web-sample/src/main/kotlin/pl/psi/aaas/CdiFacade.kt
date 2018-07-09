package pl.psi.aaas

//import pl.psi.aaas.engine.r.timeseries.
import pl.psi.aaas.engine.r.RConnectionProvider
import pl.psi.aaas.engine.r.RServeEngine
import pl.psi.aaas.usecase.CalculationDefinition
import javax.annotation.PostConstruct
import javax.ejb.Singleton
import javax.ejb.Startup
import javax.inject.Inject

@Startup
@Singleton
class CdiFacade : Facade<CalculationDefinition, Unit> {
    @Inject
    lateinit var connection: RConnectionProvider

    lateinit var engine: Engine<CalculationDefinition, Parameter<Any>, Parameter<Any>>

    @PostConstruct
    private fun init() {
        engine = RServeEngine(connection)
    }

    override fun callScript(calcDef: CalculationDefinition) {
        engine.call(calcDef)
    }
}