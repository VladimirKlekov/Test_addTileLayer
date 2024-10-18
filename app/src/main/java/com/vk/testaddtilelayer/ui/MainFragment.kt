package com.vk.testaddtilelayer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vk.testaddtilelayer.databinding.FragmentMainBinding
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.layers.LayerOptions
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.layers.TileFormat
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CreateTileDataSource
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.tiles.UrlProvider
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

        /** нужно менять тип при добавлении  addTileLayer на MapType.NONE? **/
//        binding.mapView.mapWindow.map.mapType = MapType.NONE

        /** как правильно прописывать на котлин, что бы подгружать динамически? **/
//        val urlTileProvider =
//            UrlProvider { tileId, version, features -> " https://i.postimg.cc/jjwhMKSN/map.png " + "?x=" + tileId.x + "&y=" + tileId.y + "&z=" + tileId.z }


        val urlTileProvider = UrlProvider { tileId, _, _ ->
            /** не работает **/
//            " https://i.postimg.cc/jjwhMKSN/map.png "

            /** не работает **/
            "https://github.com/VladimirKlekov/MapPng/blob/main/map.png?raw=true"
     }

        binding.mapView.mapWindow.map.addTileLayer(
            "OpenStreetMap",
            LayerOptions().setVersionSupport(false),
            CreateTileDataSource { tileDataSourceBuilder ->
                tileDataSourceBuilder.setTileFormat(TileFormat.PNG)
                tileDataSourceBuilder.setTileUrlProvider(urlTileProvider)
            }
        )


        return view
    }

    private fun getMyLocation(){
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