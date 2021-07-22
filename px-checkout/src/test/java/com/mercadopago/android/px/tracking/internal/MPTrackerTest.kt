package com.mercadopago.android.px.tracking.internal

import com.mercadopago.android.px.addons.model.Track
import com.mercadopago.android.px.addons.model.internal.Experiment
import com.mercadopago.android.px.addons.model.internal.Variant
import com.mercadopago.android.px.internal.tracking.TrackingRepository
import com.mercadopago.android.px.model.CheckoutType
import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

private const val FLOW = "/test_flow"
private const val SESSION_ID = "/my_session_id"

@RunWith(MockitoJUnitRunner::class)
class MPTrackerTest {

    @Mock
    private lateinit var track: Track

    @Mock
    private lateinit var trackWrapper: TrackWrapper

    @Mock
    private lateinit var trackingRepository: TrackingRepository

    private val flowDetail = mapOf(Pair("flow", "detail"))

    private lateinit var tracker: MPTracker

    @Before
    fun setUp() {
        tracker = MPTracker(trackingRepository)

        `when`(track.data).thenReturn(mutableMapOf())
        `when`(track.path).thenReturn("/track_path")
        `when`(track.type).thenReturn(Track.Type.EVENT)
        `when`(trackWrapper.getTrack()).thenReturn(track)

        `when`(trackingRepository.flowId).thenReturn(FLOW)
        `when`(trackingRepository.flowDetail).thenReturn(flowDetail)
        `when`(trackingRepository.sessionId).thenReturn(SESSION_ID)
    }

    @Test
    fun whenTrackThenAddFlowSessionAndExtraInfo() {
        tracker.track(trackWrapper)

        assertEquals(FLOW, track.data["flow"])
        assertEquals(flowDetail, track.data["flow_detail"])
        assertEquals(SESSION_ID, track.data["session_id"])
        assertTrue(track.data["session_time"] as Long >= 0)
        assertEquals(CheckoutType.ONE_TAP, track.data["checkout_type"])
    }

    @Test
    fun whenTrackFrictionThenAddFlowSessionAndExtraInfo() {
        `when`(track.path).thenReturn(FrictionEventTracker.PATH)
        `when`(track.data).thenReturn(mapOf(Pair("extra_info", mutableMapOf<String, Any?>())))
        tracker.track(trackWrapper)

        val extraInfo = track.data["extra_info"] as Map<*, *>
        assertEquals(FLOW, extraInfo["flow"])
        assertNull(extraInfo["flow_detail"])
        assertEquals(SESSION_ID, extraInfo["session_id"])
        assertTrue(extraInfo["session_time"] as Long >= 0)
        assertEquals(CheckoutType.ONE_TAP, extraInfo["checkout_type"])
    }

    @Test
    fun whenTrackWithSecurityEnabledThenAddSecurityEnabledData() {
        `when`(trackingRepository.securityEnabled).thenReturn(true)
        tracker = MPTracker(trackingRepository)
        tracker.track(trackWrapper)

        assertTrue(track.data["security_enabled"] as Boolean)
    }

    @Test
    fun whenTrackWithExperimentsThenAddExperimentsLabel() {
        val experiment1 = mock(Experiment::class.java)
        val experiment2 = mock(Experiment::class.java)
        val variant = mock(Variant::class.java)
        `when`(variant.name).thenReturn("Variant")
        `when`(experiment1.name).thenReturn("Experiment1")
        `when`(experiment2.name).thenReturn("Experiment2")
        `when`(experiment1.variant).thenReturn(variant)
        `when`(experiment2.variant).thenReturn(variant)

        tracker.setExperiments(listOf(experiment1, experiment2))
        tracker.track(trackWrapper)

        assertEquals("Experiment1 - Variant,Experiment2 - Variant", track.data["experiments"])
    }
}
