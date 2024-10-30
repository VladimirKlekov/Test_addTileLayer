package com.vk.testaddtilelayer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.vk.testaddtilelayer.databinding.FragmentMainBinding
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView


class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var mapView: MapView? = null
    private lateinit var userLocation: UserLocationLayer
    private val locationObjectListener = object : UserLocationObjectListener {
        override fun onObjectAdded(view: UserLocationView) = Unit
        override fun onObjectRemoved(view: UserLocationView) = Unit
        override fun onObjectUpdated(view: UserLocationView, event: ObjectEvent) {
            userLocation.cameraPosition()?.target?.let {
                mapView?.map?.move(CameraPosition(it, 15F, 0F, 0F))
            }
            userLocation.setObjectListener(null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        getMyLocation()
        tup()




        return view
    }

    private fun tup() {
        val inputListener = object : InputListener {
            override fun onMapTap(map: Map, point: Point) {
                Toast.makeText(requireContext(), "Короткое нажатие на экран", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onMapLongTap(map: Map, point: Point) {
                Toast.makeText(requireContext(), "Долгое нажатие на экран", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.mapView.map.addInputListener(inputListener)

    }


    private fun getMyLocation() {
        mapView = binding.mapView
        var mapKit: MapKit = MapKitFactory.getInstance()
        userLocation = mapKit.createUserLocationLayer(mapView!!.mapWindow)
        userLocation.isVisible = true
        userLocation.isHeadingEnabled = false
        userLocation.setObjectListener(locationObjectListener)
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        mapView = null
    }
}